package pt.upa.broker.ws;



public class Transport{

    protected String _id;
    protected String _origin;
    protected String _destination;
    protected Integer _price;
    protected String _transporterCompany;
    protected TransportStateView _state;

    public Transport(String origin, String destination, int price, TransportStateView state){
      _id = "";
      _origin = origin;
      _destination = destination;
      _transporterCompany= "";
      _price = price;
      _state = state;
    }

    public Transport(TransportView tv){
      _id = tv.getId();
      _origin = tv.getOrigin();
      _destination = tv.getDestination();
      _transporterCompany= tv.getTransporterCompany();
      _price = tv.getPrice();
      _state = tv.getState();
    }

    public TransportView toTransportView() {
      TransportView tv = new TransportView();
      tv.setId(_id);
      tv.setOrigin(_origin);
      tv.setDestination(_destination);
      tv.setTransporterCompany(_transporterCompany);
      tv.setPrice(_price);
      tv.setState(_state);
      return tv;
    }

    public String getId() {
        return _id;
    }

    public void setId(String value) {
        this._id = value;
    }

    public String getOrigin() {
        return _origin;
    }

    public void setOrigin(String value) {
        this._origin = value;
    }

    public String getDestination() {
        return _destination;
    }

    public void setDestination(String value) {
        this._destination = value;
    }


    public Integer getPrice() {
        return _price;
    }

    public void setPrice(Integer value) {
        this._price = value;
    }

    public String getTransporterCompany() {
        return _transporterCompany;
    }

    public void setTransporterCompany(String value) {
        this._transporterCompany = value;
    }

    public TransportStateView getState() {
        return _state;
    }

    public void setState(TransportStateView value) {
        this._state = value;
    }

}
