import {Component, Input, OnInit} from '@angular/core';
import {Location} from '../../../dtos/location/location';

@Component({
  selector: 'app-performances-page-entry',
  templateUrl: './performances-page-entry.component.html',
  styleUrls: ['./performances-page-entry.component.css']
})
export class PerformancesPageEntryComponent implements OnInit {

  @Input() entry: Performance;

  constructor() { }

  ngOnInit(): void {
  }

}
