import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';

const modules = [
  MatCardModule, MatListModule, MatTableModule, MatSortModule, MatPaginator, MatFormFieldModule, MatInputModule, MatButtonModule, MatSlideToggleModule, MatSnackBarModule
];

@NgModule({
  imports: modules,
  exports: modules,
})
export class MaterialModule { }