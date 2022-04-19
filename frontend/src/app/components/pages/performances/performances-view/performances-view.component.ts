import {Component, Input, OnInit} from '@angular/core';
import {LocationPage} from '../../../../dtos/location/location-page';
import {PerformanceService} from '../../../../services/performance.service';

@Component({
  selector: 'app-performances-view',
  templateUrl: './performances-view.component.html',
  styleUrls: ['./performances-view.component.css']
})
export class PerformancesViewComponent implements OnInit {
  private performanceQuery: any;

  @Input()
  appendCreate: boolean = false;

  @Input()
  buyable: boolean = true;

  constructor(private performanceService: PerformanceService) {
  }

  ngOnInit(): void {
    this.loadPerformances();
  }

  /**
   * Loads all locations with query from the database
   * @throws error 404 not found exception
   */
  private loadPerformances() {
    this.performanceService.getPerformances(this.performanceQuery).subscribe(
      (performancePage: any) => {

        },
      //this.page = locationPage;
      //if ( this.page.totalElements < 12 ) { this.performanceService.page = 0; }
      //return this.page;

      error => {
        //this.defaultServiceErrorHandling(error);
      }
    );
  }
}
