import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', [
      'isAuthenticated',
      'hasAnyRole',
      'getCurrentUser'
    ]);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    guard = new AuthGuard(authService, router);
  });

  it('should block direct employee url access for another employee', () => {
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAnyRole.and.callFake((roles: string[]) => roles.includes('EMPLOYEE'));
    authService.getCurrentUser.and.returnValue({
      username: 'EMP_001',
      loginId: 1,
      role: 'EMPLOYEE'
    });

    const route = {
      params: { employeeId: 'EMP_002' },
      data: { roles: ['ADMIN', 'HR', 'MANAGER', 'EMPLOYEE'], selfOnly: true }
    } as any;

    const result = guard.canActivate(route, { url: '/employees/EMP_002' } as any);

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/employee/profile'], { queryParams: { accessDenied: '1' } });
  });

  it('should allow direct employee url access for self', () => {
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAnyRole.and.callFake((roles: string[]) => roles.includes('EMPLOYEE'));
    authService.getCurrentUser.and.returnValue({
      username: 'EMP_001',
      loginId: 1,
      role: 'EMPLOYEE'
    });

    const route = {
      params: { employeeId: 'EMP_001' },
      data: { roles: ['ADMIN', 'HR', 'MANAGER', 'EMPLOYEE'], selfOnly: true }
    } as any;

    const result = guard.canActivate(route, { url: '/employees/EMP_001' } as any);

    expect(result).toBeTrue();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should block route when user role is not allowed', () => {
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAnyRole.and.callFake((roles: string[]) => roles.includes('EMPLOYEE'));
    authService.getCurrentUser.and.returnValue({
      username: 'EMP_001',
      loginId: 1,
      role: 'EMPLOYEE'
    });

    const route = {
      params: {},
      data: { roles: ['ADMIN', 'MANAGER'] }
    } as any;

    const result = guard.canActivate(route, { url: '/monitoring/new' } as any);

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/employee/profile'], { queryParams: { accessDenied: '1' } });
  });

  it('should block employee direct url access to admin modules', () => {
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAnyRole.and.callFake((roles: string[]) => roles.includes('EMPLOYEE'));
    authService.getCurrentUser.and.returnValue({
      username: 'EMP_001',
      loginId: 1,
      role: 'EMPLOYEE'
    });

    const route = {
      params: {},
      data: { roles: ['ADMIN', 'HR', 'MANAGER'] }
    } as any;

    const result = guard.canActivate(route, { url: '/leaves' } as any);

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/employee/profile'], { queryParams: { accessDenied: '1' } });
  });
});
