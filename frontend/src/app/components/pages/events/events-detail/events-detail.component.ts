import { Component, OnInit } from '@angular/core';
import {EventService} from '../../../../services/event.service';
import {ActivatedRoute, Router} from '@angular/router';
import {EventDto} from '../../../../dtos/event-dto';
import {Performance} from '../../../../dtos/performance/performance';

@Component({
  selector: 'app-events-detail',
  templateUrl: './events-detail.component.html',
  styleUrls: ['./events-detail.component.css']
})
export class EventsDetailComponent implements OnInit {
  public id: number;
  public event: EventDto;
  public errorMessage: string = '';
  constructor(private route: ActivatedRoute, private router: Router, private eventService: EventService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = params.id;
    });
    this.eventService.find(this.id).subscribe(
      value => {
        this.event = value;
        },
      error => {
        this.errorMessage = error.error.error;
      });
  }

  public ticket(id: number) {
    this.router.navigate(['/select_seats', id]);
  }
}
