import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MsalService } from '@azure/msal-angular';
import { from, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const msalService = inject(MsalService);
  const accounts = msalService.instance.getAllAccounts();

  if (accounts.length === 0) {
    return next(req);
  }

  const account = msalService.instance.getActiveAccount() || accounts[0];

  return from(
    msalService.instance.acquireTokenSilent({
      account: account,
      scopes: environment.azure.scopes
    })
  ).pipe(
    switchMap((response) => {
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${response.accessToken}`
        }
      });
      return next(authReq);
    })
  );
};