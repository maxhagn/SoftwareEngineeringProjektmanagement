import { Component, OnInit } from '@angular/core';
import {TicketPreview} from '../../../../dtos/ticket/ticket-preview';
import {ActivatedRoute} from '@angular/router';
import {TicketService} from '../../../../services/ticket.service';
import {HelperUtils} from '../../../../global/helper-utils.service';

@Component({
  selector: 'app-tickets-print',
  templateUrl: './tickets-print.component.html',
  styleUrls: ['./tickets-print.component.css']
})
export class TicketsPrintComponent implements OnInit {

  private id: number;
  public ticket: TicketPreview;
  public errorMessage: string = '';

  constructor( private route: ActivatedRoute, private ticketService: TicketService, private helperUtils: HelperUtils) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = params.id;
    });
    this.ticketService.getTicket(this.id).subscribe(
      value => {
        this.ticket = value;
      },
      error => {
        this.errorMessage = error.error.error;
      });
  }

  public concatSeats(): string {
    let out: string = '';
    for (const seat of this.ticket.seats) {
      out += 'Row ' + seat.row + ' Seat ' + seat.col + ', ';
    }
    return out.substr(0, out.length - 2);
  }
}
