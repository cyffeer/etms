import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { NpLvlInfoResponse } from '../../../np-lvl-info/np-lvl-info.model';
import { NpLvlInfoService } from '../../../np-lvl-info/services/np-lvl-info.service';
import { NpTestHist, NpType } from '../../models/nihongo.models';
import { NpTestHistService } from '../../services/np-test-hist.service';
import { NpTypeService } from '../../services/np-type.service';

type NihongoTestRow = {
  npTestHistId: number;
  typeLabel: string;
  levelLabel: string;
  testDate?: string | null;
  testCenter?: string | null;
  testLevel?: string | null;
  score?: number | null;
  passed?: boolean | null;
  remarks?: string | null;
};

@Component({
  selector: 'app-nihongo-tests-page',
  templateUrl: './nihongo-tests-page.component.html',
})
export class NihongoTestsPageComponent implements OnInit {
  loading = false;
  error = '';
  rows: NihongoTestRow[] = [];
  filters = {
    keyword: '',
    typeCode: '',
  };
  availableTypeCodes: string[] = [];

  constructor(
    private readonly npLvlInfoService: NpLvlInfoService,
    private readonly npTypeService: NpTypeService,
    private readonly npTestHistService: NpTestHistService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    forkJoin({
      levels: this.npLvlInfoService.getAll(),
      types: this.npTypeService.getAll(),
      tests: this.npTestHistService.getAll(),
    }).subscribe({
      next: ({ levels, types, tests }) => {
        this.availableTypeCodes = types.map((type) => type.npTypeCode).sort();
        this.rows = this.buildRows(tests, levels, types).filter((row) => this.matchesFilters(row));
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load Nihongo test schedules.';
        this.loading = false;
      },
    });
  }

  resetFilters(): void {
    this.filters = {
      keyword: '',
      typeCode: '',
    };
    this.load();
  }

  resultLabel(row: NihongoTestRow): string {
    if (row.passed == null) {
      return 'Pending';
    }
    return row.passed ? 'Passed' : 'Not Passed';
  }

  resultClass(row: NihongoTestRow): string {
    if (row.passed == null) {
      return 'status-pill--warning';
    }
    return row.passed ? 'status-pill--active' : 'status-pill--inactive';
  }

  private buildRows(tests: NpTestHist[], levels: NpLvlInfoResponse[], types: NpType[]): NihongoTestRow[] {
    const levelByCode = levels.reduce<Record<string, NpLvlInfoResponse>>((acc, level) => {
      acc[level.npLvlInfoCode] = level;
      return acc;
    }, {});

    const typeByCode = types.reduce<Record<string, NpType>>((acc, type) => {
      acc[type.npTypeCode] = type;
      return acc;
    }, {});

    return tests
      .map((test) => {
        const level = levelByCode[test.npLvlInfoCode];
        const type = level ? typeByCode[level.npTypeCode] : undefined;
        return {
          npTestHistId: test.npTestHistId,
          typeLabel: type ? `${type.npTypeCode} - ${type.npTypeName}` : (level?.npTypeCode || '-'),
          levelLabel: level ? `${level.npLvlInfoCode} - ${level.npLvlInfoName}` : test.npLvlInfoCode,
          testDate: test.testDate || null,
          testCenter: test.testCenter || null,
          testLevel: test.testLevel || null,
          score: test.score ?? null,
          passed: test.passed ?? null,
          remarks: test.remarks || null,
        };
      })
      .sort((left, right) => (right.testDate || '').localeCompare(left.testDate || ''));
  }

  private matchesFilters(row: NihongoTestRow): boolean {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const typeCode = this.filters.typeCode.trim().toLowerCase();

    const matchesKeyword = !keyword
      || row.levelLabel.toLowerCase().includes(keyword)
      || (row.testCenter || '').toLowerCase().includes(keyword)
      || (row.testLevel || '').toLowerCase().includes(keyword)
      || (row.remarks || '').toLowerCase().includes(keyword);

    const matchesType = !typeCode || row.typeLabel.toLowerCase().includes(typeCode);

    return matchesKeyword && matchesType;
  }
}
