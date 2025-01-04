import { PassedInitialConfig } from 'angular-auth-oidc-client';

export const authConfig: PassedInitialConfig = {
  config: {
              authority: 'http://localhost:8080/.well-known/openid-configuration',
              redirectUrl: window.location.origin,
              postLogoutRedirectUri: window.location.origin,
              postLoginRoute: 'notes',
              clientId: 'notes-webapp',
              scope: 'openid',
              responseType: 'code',
              silentRenew: false,
              useRefreshToken: false,
              autoCleanStateAfterAuthentication: true,
              historyCleanupOff: false,
              disablePkce: false,
              secureRoutes: ['http://localhost:8080'],
          }
}
