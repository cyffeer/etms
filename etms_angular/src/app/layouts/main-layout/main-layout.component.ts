import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Route, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { AuthService, AuthUser } from '../../core/services/auth.service';

type NavItem = { path: string; label: string; section: string };
type NavSection = { title: string; items: NavItem[] };

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements OnInit {
  navItems: NavItem[] = [];
  navSections: NavSection[] = [];
  user$!: Observable<AuthUser | null>;
  mobileNavOpen = false;
  currentPageTitle = 'Dashboard';
  accessDeniedMessage = '';
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
        this.refreshAccessDeniedMessage();
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
    this.navSections = this.groupNavItems(this.navItems);
    this.currentPageTitle = this.resolveCurrentPageTitle(this.router.url);
    this.refreshAccessDeniedMessage();
  }

  private refreshAccessDeniedMessage(): void {
    const queryParams = new URLSearchParams(this.router.url.split('?')[1] || '');
    this.accessDeniedMessage = queryParams.get('accessDenied') === '1' ? 'Access denied for your role or scope.' : '';
  }

  private buildFromAngularRoutes(): NavItem[] {
    const shell = this.router.config.find((route) => route.component === MainLayoutComponent);
    const children = shell?.children ?? [];
    const currentUser = this.authService.getCurrentUser();
    const navItems: NavItem[] = [];

    children.forEach((route) => this.collectNavItems(route, '', currentUser, navItems));
    return navItems;
  }

  private isNavigable(route: Route): boolean {
    return !!route.path && !route.redirectTo && route.path !== '**' && route.data?.['nav'] !== false;
  }

  private isAllowedForUser(route: Route, user: AuthUser | null): boolean {
    const roles = (route.data?.['navRoles'] as string[] | undefined) ?? (route.data?.['roles'] as string[] | undefined);
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

  private collectNavItems(route: Route, prefix: string, user: AuthUser | null, collector: NavItem[]): void {
    const currentPath = this.joinPath(prefix, route.path ?? '');
    if (this.isNavigable(route) && this.isAllowedForUser(route, user) && currentPath) {
      collector.push({
        path: `/${currentPath}`,
        label: (route.data?.['title'] as string) || this.humanize(route.path ?? ''),
        section: (route.data?.['navSection'] as string) || 'Modules'
      });
    }

    const navChildren = route.data?.['navChildren'] as Array<{ path: string; title: string; roles?: string[]; navSection?: string }> | undefined;
    if (navChildren?.length && this.isAllowedForUser(route, user)) {
      navChildren
        .filter((item) => this.isAllowedForRoles(item.roles, user))
        .forEach((item) => {
          const childPath = this.joinPath(currentPath, item.path);
          if (!childPath) {
            return;
          }
          collector.push({
            path: `/${childPath}`,
            label: item.title || this.humanize(item.path),
            section: item.navSection || 'Modules'
          });
        });
    }

    (route.children ?? []).forEach((child) => this.collectNavItems(child, currentPath, user, collector));
  }

  private groupNavItems(items: NavItem[]): NavSection[] {
    const grouped = new Map<string, NavItem[]>();
    items.forEach((item) => {
      const key = item.section || 'Modules';
      const sectionItems = grouped.get(key) ?? [];
      sectionItems.push(item);
      grouped.set(key, sectionItems);
    });
    return Array.from(grouped.entries()).map(([title, sectionItems]) => ({ title, items: sectionItems }));
  }

  private joinPath(prefix: string, path: string): string {
    const normalizedPrefix = (prefix || '').replace(/^\/+|\/+$/g, '');
    const normalizedPath = (path || '').replace(/^\/+|\/+$/g, '');
    if (!normalizedPrefix) {
      return normalizedPath;
    }
    if (!normalizedPath) {
      return normalizedPrefix;
    }
    return `${normalizedPrefix}/${normalizedPath}`;
  }

  private isAllowedForRoles(roles: string[] | undefined, user: AuthUser | null): boolean {
    if (!roles?.length) {
      return true;
    }
    if (!user?.role) {
      return false;
    }
    return roles.some((role) => role.toUpperCase() === user.role?.toUpperCase());
  }

  private humanize(path: string): string {
    return path.replace(/[-/]/g, ' ').replace(/\b\w/g, (match) => match.toUpperCase());
  }
}
