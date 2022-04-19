import {Component, OnInit} from '@angular/core';
import {PerformanceService} from '../../../../services/performance.service';
import {PerformanceQuery} from '../../../../dtos/performance/performance-query';
import {LocationPage} from '../../../../dtos/location/location-page';
import {LocationQuery} from '../../../../dtos/location/location-query';
import {PerformancePage} from '../../../../dtos/performance/performance-page';

@Component({
  selector: 'app-performances-page',
  templateUrl: './performances-page.component.html',
  styleUrls: ['./performances-page.component.css']
})
export class PerformancesPageComponent implements OnInit {
  performanceQuery = new PerformanceQuery();
  page = new PerformancePage();
  error = false;
  errorMessage = '';
  extendedSearchFlag: boolean;
  extendedSearchButtonText: string;

  constructor(private performanceService: PerformanceService) {
    this.performanceQuery.page = 0;
    this.extendedSearchFlag = false;
    this.extendedSearchButtonText = 'Extended Search';
    this.page.content = [];
  }

  ngOnInit(): void {
    this.loadPerformances();
  }

  /**
   * Loads all locations with query from the database
   * @throws error 404 not found exception
   */
  private loadPerformances() {
    if (this.performanceQuery.price == null) {
      this.performanceQuery.price = 0;
    }
    this.performanceService.getPerformances(this.performanceQuery).subscribe(
      (performancePage: PerformancePage) => {
        this.page = performancePage;
        if (this.page.totalElements < 12) {
          this.performanceQuery.page = 0;
        }
        return this.page;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private setSearchLevel() {
    if (this.extendedSearchFlag) {
      this.extendedSearchFlag = false;
      this.performanceQuery.hallName = null;
      this.performanceQuery.eventTitle = null;
      this.performanceQuery.dateTime = null;
      this.performanceQuery.price = null;
      this.extendedSearchButtonText = 'Extended Search';
      this.loadPerformances();
    } else {
      this.extendedSearchFlag = true;
      this.performanceQuery.mixedQuery = null;
      this.extendedSearchButtonText = 'Mixed Search';
      this.loadPerformances();
    }
  }

  private previousPage() {
    if (this.performanceQuery.page > 0) {
      this.performanceQuery.page--;
    }

  }

  private nextPage() {
    if (this.performanceQuery.page >= 0) {
      this.performanceQuery.page++;
    }
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false; // set error flag to false
  }

  /**
   * Checking the Error Status to inform user about error
   * @param error the error from the backend
   */
  private defaultServiceErrorHandling(error: any) {
    this.error = true;
    if (error.status === 0) {
      // If status is 0, the backend is probably down
      this.errorMessage = 'The backend seems not to be reachable';
    } else if (error.status === 404) {
      // If status 404, the object could not be found in the database
      this.errorMessage = 'There where no entries in the database to your query';
    } else if (error.status === 422) {
      // If status 422, the validation failed
      this.errorMessage = 'There values you entered are not supported, check the requirements';
    } else if (error.error.message === 'No message available') {
      // If no detailed error message is provided, fall back to the simple error name
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error.message;
    }
  }

}
