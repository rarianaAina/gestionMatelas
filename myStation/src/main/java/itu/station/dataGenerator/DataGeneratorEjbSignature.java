package itu.station.dataGenerator;

import javax.ejb.Local;

@Local
public interface DataGeneratorEjbSignature {
    public void generateData() throws Exception;
}
