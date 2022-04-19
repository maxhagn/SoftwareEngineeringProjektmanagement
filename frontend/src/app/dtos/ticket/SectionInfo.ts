export class SectionInfo {
  constructor(private _name: String, private _type: String) {
  }


  get name(): String {
    return this._name;
  }

  get type(): String {
    return this._type;
  }
}
