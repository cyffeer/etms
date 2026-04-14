import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { ApiError } from '../../../models/api-error.model';

@Injectable({ providedIn: 'root' })
export class ErrorService {
  private readonly errorSubject = new Subject<ApiError>();
  readonly errors$ = this.errorSubject.asObservable();

  notify(error: ApiError): void {
    this.errorSubject.next(error);
    console.error('[API ERROR]', error);
  }
}