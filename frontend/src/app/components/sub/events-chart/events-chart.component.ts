import {Component, Input, OnInit} from '@angular/core';
import * as CanvasJS from './canvasjs.min';
import {TopTenDto} from '../../../dtos/event/top-ten-dto';
import {EventData} from '../../../dtos/event/data';


@Component({
  selector: 'app-events-chart',
  templateUrl: './events-chart.component.html',
  styleUrls: ['./events-chart.component.css']
})
export class EventsChartComponent implements OnInit {
  @Input() topTen: TopTenDto[];
  public chartType: string = 'horizontalBar';
  constructor() {
    console.log('IN CHART');
  }
  public chartLabels: Array<any> = ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'];
  public chartOptions: any = {
    responsive: true
  };
  public chartDatasets: Array<any>;
  public chartColors: Array<any> = [
    {
      backgroundColor: [
        'rgba(255, 99, 132, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(255, 206, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
        'rgba(153, 102, 255, 0.2)',
        'rgba(255, 159, 64, 0.2)'
      ],
      borderColor: [
        'rgba(255,99,132,1)',
        'rgba(54, 162, 235, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(75, 192, 192, 1)',
        'rgba(153, 102, 255, 1)',
        'rgba(255, 159, 64, 1)'
      ],
      borderWidth: 2,
    }
  ];

  compareDataPointYAscend(dataPoint1, dataPoint2) {
    return dataPoint1.y - dataPoint2.y;
  }

  compareDataPointYDescend(dataPoint1, dataPoint2) {
    return dataPoint2.y - dataPoint1.y;
  }


  public chartClicked(e: any): void {
    const x = e.dataPoint.x;
    const y = e.dataPoint.y;


    console.log(this.chartDatasets);
    console.log(this.chartDatasets);
    const data = this.chartDatasets;
    for (let i = 0; i < data.length; i++) {
      if (data[i].x === x && data[i].y === y) {
        console.log('X at i = ' + i + ' = ' + data[i].x);
        console.log('Y at i = ' + i + ' = ' + data[i].y);
        console.log('ID = ' + data[i].event_id);

      }

    }
  }


  ngOnInit(): void {
    this.chartDatasets = this.getDataPoints();
    console.log('IN CHART');
    const chart = new CanvasJS.Chart('chartContainer', {
      animationEnabled: true,

      axisX: {
        labelFontSize: 20
      },

      data: [{
        type: 'bar',
        dataPoints: this.getDataPoints()
      }]
    });

    chart.options.data[0].dataPoints.sort(this.compareDataPointYAscend);
    chart.render();
  }

  private getDataPoints() {
    console.log('DATA POINTS');
    if (this.topTen === undefined) {
      return [];
    }
    console.log(this.topTen);
    const data = [];
    for (let i = 0; i < this.topTen.length; i++) {

      // @ts-ignore
      data[i] = new EventData(this.topTen[i].soldTickets, this.topTen[i].eventTitle);
    }
    return data;

  }


  onClick($event: any) {
    console.log('CLICK');
  }


}
