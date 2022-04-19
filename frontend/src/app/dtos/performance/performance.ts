import {Event} from '../event/event';
import { HallDto } from "../event/hallDto";
import DateTimeFormat = Intl.DateTimeFormat;

export class Performance {
  public id: number;
  public date: Date;
  public event: Event;
  public hall: HallDto;
  public min_price: number;

  constructor(
  ) {}

}
