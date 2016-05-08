package pt.upa;

public enum Location {
  Porto(Zone.NORTE),
  Braga(Zone.NORTE),
  VianaDoCastelo(Zone.NORTE),
  VilaReal(Zone.NORTE),
  Braganca(Zone.NORTE),
  Lisboa(Zone.CENTRO),
  Leiria(Zone.CENTRO),
  Santarem(Zone.CENTRO),
  CasteloBranco(Zone.CENTRO),
  Coimbra(Zone.CENTRO),
  Aveiro(Zone.CENTRO),
  Viseu(Zone.CENTRO),
  Guarda(Zone.CENTRO),
  Setubal(Zone.SUL),
  Evora(Zone.SUL),
  Portalegre(Zone.SUL),
  Beja(Zone.SUL),
  Faro(Zone.SUL);

  private Zone _zone;

  private Location(Zone z){
    this._zone = z;
  }

  public boolean NORTE(){
    return _zone.equals(Zone.NORTE);
  }

  public boolean SUL(){
    return _zone.equals(Zone.SUL);
  }

  public boolean CENTRO(){
    return _zone.equals(Zone.CENTRO);
  }

  public Zone getZone(){
    return _zone;
  }

  public enum Zone {
    NORTE,
    CENTRO,
    SUL
  }

  public String value() {
    return name();
  }

  public static Location fromValue(String v) {
    try{
      Location ret = null;
      if(v != null) {
        ret = valueOf(v);
      }
      return ret;
    }catch(IllegalArgumentException e){
      return null;
    }
  }
}
