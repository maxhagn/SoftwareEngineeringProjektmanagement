import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { PerformanceDetailDto } from "../dtos/performance/performance-detail-dto";
import { map } from "rxjs/operators";
import { TicketSeatDto } from "../dtos/ticket/ticket-seat-dto";
import { PerformancePage } from '../dtos/performance/performance-page';

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {

  private performanceBaseUri: string = this.globals.backendUri + '/performance/';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Used to get specific information about a performance
   *
   * @param id of the performance we want more information about
   * @return PerformanceDetailDto with information about the performance
   * */
  getPerformance(id: number): Observable<PerformanceDetailDto> {
    return this.httpClient.get<PerformanceDetailDto>(this.globals.performanceUri + "/" + id)
      .pipe<PerformanceDetailDto>(map<PerformanceDetailDto, PerformanceDetailDto>(r => {
        r.date = new Date(r.date);
        r.duration = new Date(r.duration)
        return r;
      }));
  }

  /**
   * Get all taken seats for the performance
   *
   * @param id of the performance
   * @return Array of TicketSeatDto with taken seats
   * */
  public getTakenSeats(id: number): Observable<TicketSeatDto[]> {
    return this.httpClient.get<TicketSeatDto[]>(this.globals.performanceUri + "/" + id + "/taken");
  }

  /**
   * Loads all performances with query from the backend
   */
  getPerformances(performanceQuery): Observable<any> {

    let params = new HttpParams();
    for(let key in performanceQuery) {
      if(performanceQuery[key] !== null) {
        params = params.set(key, performanceQuery[key]);
      }
    }

    return this.httpClient.get<PerformancePage>(this.performanceBaseUri, {params: params});
  }
}
