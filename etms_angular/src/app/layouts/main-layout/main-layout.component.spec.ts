import { NavigationEnd, Router } from '@angular/router';
import { of } from 'rxjs';
import { MainLayoutComponent } from './main-layout.component';
import { AuthService } from '../../core/services/auth.service';

describe('MainLayoutComponent', () => {
  let component: MainLayoutComponent;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['getCurrentUser', 'logout'], {
      user$: of({ username: 'EMP_001', role: 'EMPLOYEE', loginId: 1 })
    });
    router = jasmine.createSpyObj<Router>('Router', ['navigateByUrl'], {
      events: of(new NavigationEnd(1, '/employee/profile', '/employee/profile')),
      url: '/employee/profile',
      config: [
        {
          path: '',
          component: MainLayoutComponent,
          children: [
            { path: 'dashboard', data: { nav: true, title: 'Dashboard', roles: ['ADMIN', 'HR', 'MANAGER'] } },
            { path: 'notifications', data: { nav: true, title: 'Notifications', roles: ['ADMIN', 'HR', 'MANAGER'] } },
            { path: 'employees', data: { nav: true, title: 'Employees', roles: ['ADMIN', 'MANAGER'] } },
            {
              path: 'employee',
              data: { nav: false, roles: ['EMPLOYEE'] },
              children: [
                { path: 'profile', data: { nav: true, title: 'My Profile', roles: ['EMPLOYEE'], navSection: 'Employee' } },
                { path: 'leave', data: { nav: true, title: 'My Leave', roles: ['EMPLOYEE'], navSection: 'Employee' } }
              ]
            }
          ]
        }
      ]
    });
    authService.getCurrentUser.and.returnValue({ username: 'EMP_001', role: 'EMPLOYEE', loginId: 1 });

    component = new MainLayoutComponent(router, authService);
    component.ngOnInit();
  });

  it('shows only employee self-service modules for employee users', () => {
    const labels = component.navItems.map((x) => x.label);
    expect(labels).toContain('My Profile');
    expect(labels).toContain('My Leave');
    expect(labels).not.toContain('Dashboard');
    expect(labels).not.toContain('Notifications');
    expect(labels).not.toContain('Employees');
  });
});
