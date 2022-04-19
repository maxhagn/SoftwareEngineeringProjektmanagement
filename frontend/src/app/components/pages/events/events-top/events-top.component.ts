import {Component, OnInit} from '@angular/core';
import {TopTenQuery} from '../../../../dtos/event/top-ten-query';
import {TopTenDto} from '../../../../dtos/event/top-ten-dto';
import {EventService} from '../../../../services/event.service';
import * as CanvasJS from './canvasjs.min';
import {EventData} from '../../../../dtos/event/data';
import {Router} from '@angular/router';


@Component({
  selector: 'app-events-top',
  templateUrl: './events-top.component.html',
  styleUrls: ['./events-top.component.css']
})
export class EventsTopComponent implements OnInit {
  topTenQuery = new TopTenQuery();
  topTenEvents: TopTenDto [];
  month: string;
  year: string;
  topEventId: number;
  /*errors*/
  error: boolean = false;
  errorMessage: string = '';

  constructor(public eventService: EventService, public router: Router) {
    this.topTenQuery.category = '';
    this.topTenQuery.month = '';

  }

  ngOnInit(): void {
    this.year = '' + new Date().getFullYear();
    this.topTenQuery.category = 'ACTION';
    this.month = 'JANUARY ' + this.year ;
    this.loadTopTenEvents();
  }

  /**
   * This method loads the top ten events and plots the chart
   */
  loadTopTenEvents() {
    const date = this.getDate();

    this.topTenQuery.month = date;

    this.eventService.getTop(this.topTenQuery).subscribe(
      (topTenEvents: TopTenDto[]) => {

        this.topTenEvents = topTenEvents;
        if (topTenEvents.length !== 0) {
          this.plotChart();
        } else {
          const chart = new CanvasJS.Chart('chartContainer', null);
        }

      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );

  }

  /**
   * This method converts the name of the month into a date
   */
  private getDate() {
    let date;
    this.year = this.month.split(' ')[1];

    this.topTenQuery.month = this.month;
    switch (this.topTenQuery.month) {
      case 'JANUARY ' + this.year:
        date = this.year + '-01-01';
        break;
      case 'FEBRUARY ' + this.year:
        date = this.year + '-02-01';
        break;
      case 'MARCH ' + this.year:
        date = this.year + '-03-01';
        break;
      case 'APRIL ' + this.year:
        date = this.year + '-04-01';
        break;
      case 'MAY ' + this.year:
        date = this.year + '-05-01';
        break;
      case 'JUNE ' + this.year:
        date = this.year + '-06-01';
        break;
      case 'JULY ' + this.year:
        date = this.year + '-07-01';
        break;
      case 'AUGUST ' + this.year:
        date = this.year + '-08-01';
        break;
      case 'SEPTEMBER ' + this.year:
        date = this.year + '-09-01';
        break;
      case 'OCTOBER ' + this.year:
        date = this.year + '-10-01';
        break;
      case 'NOVEMBER ' + this.year:
        date = this.year + '-11-01';
        break;
      case 'DECEMBER ' + this.year:
        date = this.year + '-12-01';
        break;
      default:
        date = '2020-01-01';

    }

    return date;
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

  /**
   * This method plots the chart
   */
  plotChart() {
    const chart = new CanvasJS.Chart('chartContainer', {
      animationEnabled: true,

      axisX: {
        labelFontSize: 20
      },

      data: [{
        type: 'bar',
        dataPoints: this.getDataPoints(),
        click: function (e) {
          const x = e.dataPoint.x;
          const y = e.dataPoint.y;


          const data = chart.options.data[0].dataPoints;
          for (let i = 0; i < data.length; i++) {
            if (data[i].x === x && data[i].y === y) {

              window.location.replace('http://localhost:4200/event/' + data[i].event_id);


            }

          }

        }
      }]
    });

    chart.options.data[0].dataPoints.sort(this.compareDataPointYAscend);
    chart.render();


  }

  /**
   * This method gets the data points
   */
  private getDataPoints() {

    if (this.topTenEvents === undefined) {
      return [];
    }

    const data = [];
    for (let i = 0; i < this.topTenEvents.length; i++) {
      data[i] = new EventData(this.topTenEvents[i].event_id, this.topTenEvents[i].soldTickets, this.topTenEvents[i].eventTitle);
    }
    return data;

  }

  /**
   * this method compares the data point y ascending
   * @param dataPoint1 first data point
   * @param dataPoint2 second data point
   */
  compareDataPointYAscend(dataPoint1, dataPoint2) {
    return dataPoint1.y - dataPoint2.y;
  }

  /**
   * this method compares the data point y descending
   * @param dataPoint1 first data point
   * @param dataPoint2 second data point
   */
  compareDataPointYDescend(dataPoint1, dataPoint2) {
    return dataPoint2.y - dataPoint1.y;
  }


  /**
   * This method gets the months
   */
  getMonth() {
    let month;
    switch (this.topTenQuery.month) {
      case  this.year + '-01-01':
        month = 'January';
        break;
      case  this.year + '-02-01':
        month = 'February';
        break;
      case this.year + '-03-01':
        month = 'March';
        break;
      case this.year + '-04-01':
        month = 'April';
        break;
      case this.year + '-05-01':
        month = 'May';
        break;
      case this.year + '-06-01':
        month = 'June';
        break;
      case this.year + '-07-01':
        month = 'July';
        break;
      case  this.year + '-08-01':
        month = 'August';
        break;
      case this.year + '-09-01':
        month = 'September';
        break;
      case this.year + '-10-01':
        month = 'October';
        break;
      case this.year + '-11-01':
        month = 'November';
        break;
      case this.year + '-12-01':
        month = 'December';
        break;

    }

    return month;
  }

  /**
   * This method gets the year before the current year
   */
  getYearBefore() {
    this.year = '' + (new Date().getFullYear() - 1);
    return ' ' + (new Date().getFullYear() - 1);
  }

  /**
   * This method gets the year
   */
  getYear() {
    return this.year;
  }

  /**
   * This method gets the current year
   */
  getCurrYear() {
    this.year = '' + (new Date().getFullYear());
    return this.year;
  }
}
