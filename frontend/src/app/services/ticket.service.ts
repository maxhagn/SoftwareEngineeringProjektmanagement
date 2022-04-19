import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Globals } from '../global/globals';
import { TicketPreview } from '../dtos/ticket/ticket-preview';
import { TicketPage } from '../dtos/ticket/ticket-page';
import { TicketQueueDto } from "../dtos/ticket/ticket-queue-dto";
import { TicketCheckoutDto } from "../dtos/ticket/ticket-checkout-dto";
import { SeatSelectDto } from "../dtos/seat/seat-select-dto";
import { SeatUnselectDto } from "../dtos/seat/seat-unselect-dto";
import { TicketSeatDto } from "../dtos/ticket/ticket-seat-dto";


@Injectable({
  providedIn: 'root'
})
export class TicketService {

  private messageBaseUri: string = this.globals.backendUri + '/ticket/';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  public getTicket(id: number): Observable<TicketPreview> {
    return this.httpClient.get<TicketPreview>(this.globals.ticketUri + '/' + id);
  }

  public getInvoice(id: number): Observable<Blob> {
    return this.httpClient.get(this.globals.ticketUri + '/' + id + '/invoice', { responseType: 'blob' });
  }

  public getCancellationInvoice(id: number): Observable<Blob> {
    return this.httpClient.get(this.globals.ticketUri + '/' + id + '/storno', { responseType: 'blob' });
  }

  //TODO rename
  /**
   * Loads all tickets from active user
   */
  getFilteredTickets(ticketQuery): Observable<TicketPage> {

    let params = new HttpParams();
    for(let key in ticketQuery) {
      if(ticketQuery[key] !== null) {
        params = params.set(key, ticketQuery[key]);
      }
    }

    return this.httpClient.get<TicketPage>(this.messageBaseUri, {params: params});
  }
  /**
   * Gets a ticket which is alread queued for the current user or creates a new one
   *
   * @param id of the performance for which to search or create a new ticket
   * @return TicketQueueDto with the information about the queued ticket
   * */
  public getOrCreateTicket(id: number): Observable<TicketQueueDto> {
    return this.httpClient.post<TicketQueueDto>(this.globals.ticketUri + "/" + id + "/queue", {});
  }

  /**
   * Get information about a ticket for checkout
   *
   * @param id of the ticket to get
   * @return TicketCheckoutDto with information about the ticket
   * */
  public getCheckoutTicketInfo(id: number): Observable<TicketCheckoutDto> {
    return this.httpClient.get<TicketCheckoutDto>(this.globals.ticketUri + "/" + id + "/checkout")
      .pipe<TicketCheckoutDto>(map<TicketCheckoutDto, TicketCheckoutDto>(r => {
        r.date = new Date(r.date);
        return r
      }));
  }

  /**
   * Get all selected seats for the ticket
   *
   * @param id of the ticket
   * @return Array of TicketSeatDto with selected seats
   * */
  public getSelectedSeats(id: number): Observable<TicketSeatDto[]> {
    return this.httpClient.get<TicketSeatDto[]>(this.globals.ticketUri + "/" + id + "/seats");
  }

  /**
   * Select a specific seat or area
   *
   * @param seatSelectDto infos about which seat to select for which ticket
   * */
  public selectSeat(seatSelectDto: SeatSelectDto): Observable<any> {
    return this.httpClient.post(this.globals.ticketUri + "/select/", {
      ticketId: seatSelectDto.ticketId,
      type: seatSelectDto.type,
      row: seatSelectDto.row,
      col: seatSelectDto.col,
      areaId: seatSelectDto.areaId,
      seatCount: seatSelectDto.seatCount
    });
  }

  /**
   * Unselect a specific seat or area
   *
   * @param seatUnselectDto infos about which seat to unselect for which ticket
   * */
  public unselectSeat(seatUnselectDto: SeatUnselectDto): Observable<any> {
    return this.httpClient.post(this.globals.ticketUri + "/unselect/", {
      ticketId: seatUnselectDto.ticketId,
      type: seatUnselectDto.type,
      row: seatUnselectDto.row,
      col: seatUnselectDto.col,
      areaId: seatUnselectDto.areaId
    });
  }

  /**
   * Buy a ticket
   *
   * @param id of the ticket to buy
   * */
  public buyTicket(id: number): Observable<any> {
    return this.httpClient.patch(this.globals.ticketUri + "/" + id + "/buy", {});
  }

  /**
   * Reserve a ticket
   *
   * @param id of the ticket to reserve
   * */
  public reserveTicket(id: number): Observable<any> {
    return this.httpClient.patch(this.globals.ticketUri + "/" + id + "/reserve", {});
  }

  /**
   * Cancel a ticket reservation
   *
   * @param id of the ticket to cancel
   * */
  public cancelReservedTicket(id: number): Observable<any> {
    return this.httpClient.post(this.globals.ticketUri + "/" + id + "/cancelReserved", {});
  }

  /**
   * Cancel a bought ticket
   *
   * @param id of the ticket to cancel
   * */
  public cancelBoughtTicket(id: number): Observable<any> {
    return this.httpClient.post(this.globals.ticketUri + "/" + id + "/cancelBought", {});
  }

}
