import {Seat} from './seat';
import {PerformanceTicket} from './performance-ticket';

export class Ticket {
  public id: string;
  public date: string;
  public status: string;
  public seats: Seat[];
  public performance: PerformanceTicket;

  constructor(

  ) {}
}
