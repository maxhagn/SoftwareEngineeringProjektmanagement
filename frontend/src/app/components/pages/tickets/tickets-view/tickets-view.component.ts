import {Component, OnInit} from '@angular/core';
import {LocationPage} from '../../../../dtos/location/location-page';
import {AuthService} from '../../../../services/auth.service';
import {LocationService} from '../../../../services/location.service';
import {TicketService} from '../../../../services/ticket.service';
import {TicketPage} from '../../../../dtos/ticket/ticket-page';
import {TicketQuery} from '../../../../dtos/ticket/ticket-query';

@Component({
  selector: 'app-tickets-view',
  templateUrl: './tickets-view.component.html',
  styleUrls: ['./tickets-view.component.css']
})
export class TicketsViewComponent implements OnInit {
  boughtPage: TicketPage = new TicketPage();
  reservedPage: TicketPage = new TicketPage();
  boughtTicketQuery = new TicketQuery();
  reservedTicketQuery = new TicketQuery();
  error = false;
  errorMessage = '';

  constructor(public authService: AuthService, private ticketService: TicketService) {
    this.reservedTicketQuery.page = 0;
    this.boughtTicketQuery.page = 0;
  }

  ngOnInit(): void {
    this.loadBoughtTickets();
    this.loadReservedTickets();
  }

  private loadReservedTickets() {
    this.reservedTicketQuery.status = 'RESERVED';
    this.ticketService.getFilteredTickets(this.reservedTicketQuery).subscribe(
      (ticketPage: any) => {
        this.reservedPage = ticketPage;
        if (this.reservedPage.totalElements < 4) {
          this.reservedTicketQuery.page = 0;
        }
        return this.reservedPage;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private loadBoughtTickets() {
    this.boughtTicketQuery.status = 'BOUGHT';
    this.ticketService.getFilteredTickets(this.boughtTicketQuery).subscribe(
      (ticketPage: any) => {
        this.boughtPage = ticketPage;
        if (this.boughtPage.totalElements < 4) {
          this.boughtTicketQuery.page = 0;
        }
        return this.boughtPage;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private previousBoughtPage() {
    if (this.boughtTicketQuery.page > 0) {
      this.boughtTicketQuery.page--;
    }
  }

  private nextBoughtPage() {
    if (this.boughtTicketQuery.page >= 0) {
      this.boughtTicketQuery.page++;
    }
  }

  private previousReservedPage() {
    if (this.reservedTicketQuery.page > 0) {
      this.reservedTicketQuery.page--;
    }
  }

  private nextReservedPage() {
    if (this.reservedTicketQuery.page >= 0) {
      this.reservedTicketQuery.page++;
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
