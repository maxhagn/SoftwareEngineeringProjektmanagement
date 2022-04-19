import {Reader} from './readers-dto';

export class NewsReadersDto {
  constructor(
    public id: number,
    public readers: Reader[]) {
  }
}
