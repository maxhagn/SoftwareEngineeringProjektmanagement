import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: '[app-performances-entry]',
  templateUrl: './performances-entry.component.html',
  styleUrls: ['./performances-entry.component.css']
})
export class PerformancesEntryComponent implements OnInit {

  @Input()
  buyable: boolean;

  constructor() { }

  ngOnInit(): void {
  }

}
