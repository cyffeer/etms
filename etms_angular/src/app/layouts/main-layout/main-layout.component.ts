import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Route, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { AuthService, AuthUser } from '../../core/services/auth.service';

type NavItem = { path: string; label: string };

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements OnInit {
  navItems: NavItem[] = [];
  user$!: Observable<AuthUser | null>;
  mobileNavOpen = false;
  currentPageTitle = 'Dashboard';
  constructor(private readonly router: Router, private readonly authService: AuthService) {}

  ngOnInit(): void {
    this.user$ = this.authService.user$;
    this.refreshNavigation();

    this.user$.subscribe(() => this.refreshNavigation());
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe(() => {
        this.mobileNavOpen = false;
        this.refreshNavigation();
      });
  }

  toggleMobileNav(): void {
    this.mobileNavOpen = !this.mobileNavOpen;
  }

  closeMobileNav(): void {
    this.mobileNavOpen = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }

  private refreshNavigation(): void {
    this.navItems = this.buildFromAngularRoutes();
    this.currentPageTitle = this.resolveCurrentPageTitle(this.router.url);
  }

  private buildFromAngularRoutes(): NavItem[] {
    const shell = this.router.config.find((route) => route.component === MainLayoutComponent);
    const children = shell?.children ?? [];
    const currentUser = this.authService.getCurrentUser();

    return children
      .filter((route) => this.isNavigable(route) && this.isAllowedForUser(route, currentUser))
      .map((route) => ({
        path: '/' + (route.path ?? ''),
        label: (route.data?.['title'] as string) || this.humanize(route.path ?? '')
      }));
  }

  private isNavigable(route: Route): boolean {
    return !!route.path && !route.redirectTo && route.path !== '**' && route.data?.['nav'] !== false;
  }

  private isAllowedForUser(route: Route, user: AuthUser | null): boolean {
    const roles = route.data?.['roles'] as string[] | undefined;
    if (!roles?.length) {
      return true;
    }
    if (!user?.role) {
      return false;
    }
    return roles.some((role) => role.toUpperCase() === user.role?.toUpperCase());
  }

  private resolveCurrentPageTitle(url: string): string {
    const cleanedUrl = (url || '/dashboard').split('?')[0];
    const match = this.navItems.find((item) => cleanedUrl === item.path || cleanedUrl.startsWith(`${item.path}/`));
    return match?.label || 'Dashboard';
  }

  private humanize(path: string): string {
    return path.replace(/[-/]/g, ' ').replace(/\b\w/g, (match) => match.toUpperCase());
  }
}
