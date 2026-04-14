import { Component, OnInit } from '@angular/core';
import { Route, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService, AuthUser } from '../../core/services/auth.service';

type NavItem = { path: string; label: string; description?: string };

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements OnInit {
  navItems: NavItem[] = [];
  user$!: Observable<AuthUser | null>;

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    this.navItems = this.buildFromAngularRoutes();
    this.user$ = this.authService.user$;
  }

  private buildFromAngularRoutes(): NavItem[] {
    const shell = this.router.config.find(r => r.component === MainLayoutComponent);
    const children = shell?.children ?? [];
    return children
      .filter(r => this.isNavigable(r))
      .map(r => ({
        path: '/' + (r.path ?? ''),
        label: (r.data?.['title'] as string) || this.humanize(r.path ?? ''),
        description: r.data?.['description'] as string | undefined
      }));
  }

  private isNavigable(r: Route): boolean {
    return !!r.path && !r.redirectTo && r.path !== '**' && r.data?.['nav'] !== false;
  }

  private humanize(path: string): string {
    return path.replace(/[-/]/g, ' ').replace(/\b\w/g, m => m.toUpperCase());
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
