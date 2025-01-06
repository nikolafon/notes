import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpStatusCode } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, map, Observable, throwError } from "rxjs";

@Injectable()
export class CustomAuthInterceptor implements HttpInterceptor {

  private router = inject(Router);

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      map((event: HttpEvent<any>) => {
        return event;
      }),
      catchError(
        (
          httpErrorResponse: HttpErrorResponse,
          _: Observable<HttpEvent<any>>
        ) => {
          if (httpErrorResponse.status === HttpStatusCode.Unauthorized) {
            sessionStorage.clear();
            this.router.navigate(['/login']);
          }
          return throwError(httpErrorResponse);
        }
      )
    );
  }
}