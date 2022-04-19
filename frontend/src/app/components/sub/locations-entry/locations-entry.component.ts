import {Component, Input, OnInit} from '@angular/core';
import {Location} from '../../../dtos/location/location';

@Component({
  selector: 'app-locations-entry',
  templateUrl: './locations-entry.component.html',
  styleUrls: ['./locations-entry.component.css']
})
export class LocationsEntryComponent implements OnInit {

  @Input() entry: Location;

  constructor() { }

  ngOnInit(): void {
  }

}
