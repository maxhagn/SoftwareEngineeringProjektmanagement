import {Event} from '../event/event';

export class ArtistWithEvents {
  constructor(
    public id: string,
    public firstname: string,
    public surname: string,
    public events: Event[]
  ) {}
}
