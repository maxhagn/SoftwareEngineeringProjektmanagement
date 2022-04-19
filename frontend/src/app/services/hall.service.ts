import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { HallDto } from '../dtos/event/hallDto';
import { PerformanceDetailDto } from "../dtos/performance/performance-detail-dto";
import { map } from "rxjs/operators";
import { TicketSeatDto } from "../dtos/ticket/ticket-seat-dto";

@Injectable({
  providedIn: 'root'
})
export class HallService {

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Used to get specific information about a hall
   * @param id of the hall we want more information about
   * */
  getHall(id: number): Observable<HallDto> {
    return this.httpClient.get<HallDto>(this.globals.hallUri + "/" + id);
  }
}
