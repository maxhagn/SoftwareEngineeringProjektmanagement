import {HallDto} from '../event/hallDto';

export class Location {
  constructor(
    public id: string,
    public name: string,
    public street: string,
    public city: string,
    public area_code: string,
    public halls: HallDto[]
  ) {}
}
