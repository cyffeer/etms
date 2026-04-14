import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiError } from '../../models/api-error.model';
import { ErrorService } from '../services/error/error.service';

@Injectable()
export class ApiErrorInterceptor implements HttpInterceptor {
  constructor(private errorService: ErrorService) {}

  intercept(req: HttpRequest<unknown>, next: import('@angular/common/http').HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        const normalized: ApiError = {
          status: error.status || 0,
          message: this.resolveMessage(error),
          details: error.error,
          timestamp: new Date().toISOString()
        };

        this.errorService.notify(normalized);
        return throwError(() => normalized);
      })
    );
  }

  private resolveMessage(error: HttpErrorResponse): string {
    if (error.error?.message) return error.error.message;
    if (error.status === 0) return 'Network error: backend may be unreachable.';
    if (error.status >= 500) return 'Server error. Please try again later.';
    if (error.status === 404) return 'Resource not found.';
    if (error.status === 401) return 'Unauthorized request.';
    return 'Request failed.';
  }
}