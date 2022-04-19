import {Event} from '../event/event';
export class LocationWithEvents {
  constructor(
    public id: string,
    public name: string,
    public street: string,
    public city: string,
    public area_code: string,
    public events: Event[]
  ) {}
}
