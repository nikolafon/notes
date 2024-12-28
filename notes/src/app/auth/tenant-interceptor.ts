import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export class TenantInterceptor implements HttpInterceptor {

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (!request.headers.has('X-Tenant-Id')) {
            request = request.clone({
                headers: request.headers.set('X-Tenant-Id', localStorage.getItem('tenantId') || '')
            });
        }
        return next.handle(request);
    }
}