import {Event} from '../event/event';
import { HallDto } from "../event/hallDto";
import DateTimeFormat = Intl.DateTimeFormat;

export class PerformanceTicket {
  public id: number;
  public datetime: Date;
  public event: Event;
  public hall: HallDto;
  public min_price: number;

  constructor(
  ) {}

}
