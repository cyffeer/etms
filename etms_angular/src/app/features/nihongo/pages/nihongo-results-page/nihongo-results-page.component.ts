import { Component, OnInit } from '@angular/core';
import { forkJoin, map } from 'rxjs';
import { EmployeeResponse } from '../../../employees/models/employee.model';
import { EmployeesService } from '../../../employees/services/employees.service';
import { NpLvlInfoResponse } from '../../../np-lvl-info/np-lvl-info.model';
import { NpLvlInfoService } from '../../../np-lvl-info/services/np-lvl-info.service';
import { NpTestEmpHist, NpTestHist, NpType } from '../../models/nihongo.models';
import { NpTestEmpHistService } from '../../services/np-test-emp-hist.service';
import { NpTestHistService } from '../../services/np-test-hist.service';
import { NpTypeService } from '../../services/np-type.service';

type NihongoResultRow = {
  employeeNumber: string;
  employeeName: string;
  typeLabel: string;
  levelLabel: string;
  allowanceStartDate?: string | null;
  allowanceEndDate?: string | null;
  testDate?: string | null;
  testCenter?: string | null;
  testLevel?: string | null;
  policyValidTo?: string | null;
  expired?: boolean | null;
  pointsLabel: string;
  resultLabel: string;
  resultClass: string;
  takeFlagLabel: string;
};

@Component({
  selector: 'app-nihongo-results-page',
  templateUrl: './nihongo-results-page.component.html',
})
export class NihongoResultsPageComponent implements OnInit {
  loading = false;
  error = '';
  rows: NihongoResultRow[] = [];
  summary = {
    total: 0,
    passed: 0,
    employees: 0,
  };

  filters = {
    employeeNumber: '',
    passedOnly: false,
    mostRecentOnly: true,
  };

  constructor(
    private readonly employeesService: EmployeesService,
    private readonly npLvlInfoService: NpLvlInfoService,
    private readonly npTypeService: NpTypeService,
    private readonly npTestHistService: NpTestHistService,
    private readonly npTestEmpHistService: NpTestEmpHistService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    forkJoin({
      employees: this.employeesService.getEmployees().pipe(map((result) => result.items)),
      levels: this.npLvlInfoService.getAll(),
      types: this.npTypeService.getAll(),
      tests: this.npTestHistService.getAll(),
      results: this.npTestEmpHistService.search(this.filters),
    }).subscribe({
      next: ({ employees, levels, types, tests, results }) => {
        this.rows = this.buildRows(results, employees, levels, types, tests);
        this.summary = {
          total: this.rows.length,
          passed: this.rows.filter((row) => row.resultLabel === 'Passed').length,
          employees: new Set(this.rows.map((row) => row.employeeNumber)).size,
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load Nihongo employee results.';
        this.loading = false;
      },
    });
  }

  resetFilters(): void {
    this.filters = {
      employeeNumber: '',
      passedOnly: false,
      mostRecentOnly: true,
    };
    this.load();
  }

  policyStatusLabel(validTo?: string | null): string {
    if (!validTo) {
      return '-';
    }

    const validToDate = new Date(validTo);
    if (Number.isNaN(validToDate.getTime())) {
      return validTo;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    validToDate.setHours(0, 0, 0, 0);
    return validToDate < today ? 'EXPIRED' : validTo;
  }

  policyStatusClass(validTo?: string | null): string {
    if (!validTo) {
      return 'status-pill--neutral';
    }

    const validToDate = new Date(validTo);
    if (Number.isNaN(validToDate.getTime())) {
      return 'status-pill--neutral';
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    validToDate.setHours(0, 0, 0, 0);
    return validToDate < today ? 'status-pill--inactive' : 'status-pill--active';
  }

  private buildRows(
    results: NpTestEmpHist[],
    employees: EmployeeResponse[],
    levels: NpLvlInfoResponse[],
    types: NpType[],
    tests: NpTestHist[]
  ): NihongoResultRow[] {
    const employeeByCode = employees.reduce<Record<string, EmployeeResponse>>((acc, employee) => {
      acc[employee.employeeCode] = employee;
      return acc;
    }, {});

    const testById = tests.reduce<Record<number, NpTestHist>>((acc, test) => {
      acc[test.npTestHistId] = test;
      return acc;
    }, {});

    return results
      .map((result) => {
        const employee = employeeByCode[result.employeeNumber];
        const test = testById[result.npTestHistId];
        const level = levels.find((item) => item.npLvlInfoCode === (result.npLvlInfoCode || test?.npLvlInfoCode));
        const type = types.find((item) => item.npTypeCode === (result.npTypeCode || level?.npTypeCode));
        const policyValidTo = result.effectiveAllowanceEndDate || result.allowanceEndDate || level?.validTo || null;

        return {
          employeeNumber: result.employeeNumber,
          employeeName: employee ? `${employee.firstName} ${employee.lastName}` : 'Unknown Employee',
          typeLabel: type ? `${type.npTypeCode} - ${type.npTypeName}` : (result.npTypeName || result.npTypeCode || level?.npTypeCode || '-'),
          levelLabel: level ? `${level.npLvlInfoCode} - ${level.npLvlInfoName}` : (result.npLvlInfoName || result.npLvlInfoCode || test?.npLvlInfoCode || '-'),
          allowanceStartDate: result.allowanceStartDate || null,
          allowanceEndDate: result.allowanceEndDate || null,
          testDate: test?.testDate || null,
          testCenter: test?.testCenter || null,
          testLevel: test?.testLevel || null,
          policyValidTo,
          expired: result.expired ?? null,
          pointsLabel: this.buildPointsLabel(result.points, test?.score),
          resultLabel: this.buildResultLabel(result),
          resultClass: this.buildResultClass(result),
          takeFlagLabel: result.takeFlag ? 'Taken' : 'Not Taken',
        };
      })
      .sort((left, right) => {
        if (left.employeeNumber !== right.employeeNumber) {
          return left.employeeNumber.localeCompare(right.employeeNumber);
        }
        return (right.testDate || '').localeCompare(left.testDate || '');
      });
  }

  private buildPointsLabel(points?: number | null, totalScore?: number | null): string {
    if (points == null && totalScore == null) {
      return '-';
    }
    if (points != null && totalScore != null) {
      return `${points} / ${totalScore}`;
    }
    return String(points ?? totalScore);
  }

  private buildResultLabel(result: NpTestEmpHist): string {
    if (result.passFlag) {
      return 'Passed';
    }
    if (result.takeFlag) {
      return 'Not Passed';
    }
    return 'Pending';
  }

  private buildResultClass(result: NpTestEmpHist): string {
    if (result.passFlag) {
      return 'status-pill--active';
    }
    if (result.takeFlag) {
      return 'status-pill--inactive';
    }
    return 'status-pill--warning';
  }
}
