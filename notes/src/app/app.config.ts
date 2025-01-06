import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { authConfig } from './auth/auth.config';
import { AuthInterceptor, provideAuth } from 'angular-auth-oidc-client';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { CookieInterceptor } from './auth/cookie-interceptor';
import { TenantInterceptor } from './auth/tenant-interceptor';
import { CustomAuthInterceptor } from './auth/custom-auth-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [provideHttpClient(withFetch(), withInterceptorsFromDi()), provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes), provideAuth(authConfig), { provide: HTTP_INTERCEPTORS, useClass: CookieInterceptor, multi: true }, { provide: HTTP_INTERCEPTORS, useClass: TenantInterceptor, multi: true }, { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }, { provide: HTTP_INTERCEPTORS, useClass: CustomAuthInterceptor, multi: true }, provideAnimationsAsync()]
};
