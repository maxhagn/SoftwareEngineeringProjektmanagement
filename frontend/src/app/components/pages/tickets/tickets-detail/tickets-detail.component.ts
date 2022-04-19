import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {TicketPreview} from '../../../../dtos/ticket/ticket-preview';
import {TicketService} from '../../../../services/ticket.service';
import {HelperUtils} from '../../../../global/helper-utils.service';
import {Seat} from '../../../../dtos/seat/seat';

@Component({
  selector: 'app-tickets-detail',
  templateUrl: './tickets-detail.component.html',
  styleUrls: ['./tickets-detail.component.css']
})
export class TicketsDetailComponent implements OnInit {

  private _printIframe: HTMLIFrameElement;
  private id: number;
  public ticket: TicketPreview;
  public ticketsAsSeats: Seat[] = new Array<Seat>();
  public ticketsAsSections: Map<String, Seat[]> = new Map<String, Seat[]>();
  public errorMessage: string = '';

  constructor(private route: ActivatedRoute, private ticketService: TicketService, private helperUtils: HelperUtils,
              private router: Router) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = params.id;
    });
    this.loadTicket();
  }

  public loadTicket() {
    this.ticketService.getTicket(this.id).subscribe(
      value => {
        this.ticket = value;
        this.ticket.seats.forEach(
          (seat: Seat) => {
            if (seat.sectionInfo.type === 'SEATS') {
              this.ticketsAsSeats.push(seat);
            } else {
              if (this.ticketsAsSections.get(seat.sectionInfo.name) == null) {
                const tmpSeats: Seat[] = new Array<Seat>();
                tmpSeats.push(seat);
                this.ticketsAsSections.set(seat.sectionInfo.name, tmpSeats);
              } else {
                const addedSeats: Seat[] = new Array<Seat>();
                this.ticketsAsSections.get(seat.sectionInfo.name).forEach(
                  (s: Seat) => {
                    addedSeats.push(s);
                  }
                );
                addedSeats.push(seat);
                this.ticketsAsSections.set(seat.sectionInfo.name, addedSeats);
              }
            }
          }
        );
      },
      error => {
        this.router.navigate(['/tickets']);
      });
  }

  public getKeys(): String[] {
    return Array.from(this.ticketsAsSections.keys());
  }

  public getValues(key: String): Seat[] {
    return Array.from(this.ticketsAsSections.get(key));
  }

  public concatSeatString(seats: Seat[]) {
    let out: String = '';
    seats.forEach(
      (seat: Seat) => {
        out += 'Row ' + seat.row + ' Seat ' + seat.col + ', ';
      }
    );
    return out.substr(0, out.length - 2);
  }


  printTicket() {
    let iframe = this._printIframe;
    if (!this._printIframe) {
      iframe = this._printIframe = document.createElement('iframe');
      document.body.appendChild(iframe);

      iframe.style.display = 'none';
      iframe.onload = function () {
        setTimeout(function () {
          iframe.focus();
          iframe.contentWindow.print();
        }, 1000);
      };
    }
    iframe.src = 'http://localhost:4200/ticket/' + this.id + '/print';
  }

  downloadInvoice() {
    this.ticketService.getInvoice(this.id)
      .subscribe(x => {
        const data = window.URL.createObjectURL(x);

        const link = document.createElement('a');
        link.href = data;
        link.download = 'Invoice.pdf';
        link.click();
      }, async error => {
        const text = await (error.error.text());
        this.errorMessage = JSON.parse(text).error;
      });
  }

  downloadCancellationInvoice() {
    this.ticketService.getCancellationInvoice(this.id)
      .subscribe(x => {
        const data = window.URL.createObjectURL(x);

        const link = document.createElement('a');
        link.href = data;
        link.download = 'ReversalInvoice.pdf';
        link.click();
      }, async error => {
        const text = await (error.error.text());
        this.errorMessage = JSON.parse(text).error;
      });
  }

  public cancelTicket() {
    if (confirm('Are you sure you want to cancel the ticket?')) {
      if (this.ticket.status == 'RESERVED') {
        this.ticketService.cancelReservedTicket(this.ticket.id).subscribe(
          () => {
            this.loadTicket();
          },
          (error) => {
            this.errorMessage = this.helperUtils.getErrorText(error);
          }
        );
      } else if (this.ticket.status == 'BOUGHT') {
        this.ticketService.cancelBoughtTicket(this.ticket.id).subscribe(
          () => {
            this.loadTicket();
          },
          (error) => {
            this.errorMessage = this.helperUtils.getErrorText(error);
          }
        );
      }
    }
  }
}
