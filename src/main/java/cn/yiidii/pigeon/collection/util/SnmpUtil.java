package cn.yiidii.pigeon.collection.util;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SnmpUtil {
    public static final int DEFAULT_VERSION = SnmpConstants.version2c;
    public static final String DEFAULT_PROTOCOL = "udp";
    public static final int DEFAULT_PORT = 161;
    public static final long DEFAULT_TIMEOUT = 3 * 1000L;
    public static final int DEFAULT_RETRY = 3;

    protected String ip;
    protected String community;

    public SnmpUtil(String ip, String community) {
        this.ip = ip;
        this.community = community;
    }

    public String snmpGet(String oid) throws IOException {
        CommunityTarget target = this.createDefault(ip, community);
        TransportMapping transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));// pcName
        pdu.setType(PDU.GET);
        return readResponse(snmp.send(pdu, target));
    }

    public List<TableEvent> snmpTableGet(String oids[]) throws IOException {
        OID[] oidArr = new OID[oids.length];
        for (int i = 0; i < oids.length; i++) {
            oidArr[i] = new OID(oids[i]);
        }

        TransportMapping transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));
        CommunityTarget target = this.createDefault(ip, community);
        List<TableEvent> tableEventList = tableUtils.getTable(target, oidArr, null, null);
        return tableEventList;
    }

    public ArrayList<String> snmpWalk(String oid) {
        ArrayList<String> result = new ArrayList<String>();
        TransportMapping transport = null;
        Snmp snmp = null;
        try {
            CommunityTarget target = this.createDefault(ip, community);
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();

            PDU pdu = new PDU();
            OID targetOID = new OID(oid);
            pdu.add(new VariableBinding(targetOID));

            boolean finished = false;
            while (!finished) {
                VariableBinding vb = null;
                ResponseEvent respEvent = snmp.getNext(pdu, target);
                PDU response = respEvent.getResponse();
                if (null == response) {
                    finished = true;
                    break;
                } else {
                    vb = response.get(0);
                }
                // check finish
                finished = checkWalkFinished(targetOID, pdu, vb);
                if (!finished) {
                    result.add(vb.toString());
                    pdu.setRequestID(new Integer32(0));
                    pdu.set(0, vb);
                } else {
                    System.out.println("SNMP walk OID 结束.");
                    snmp.close();
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex1) {
                    snmp = null;
                }
            }
        }
        return null;
    }

    private CommunityTarget createDefault(String ip, String community) {
        Address targetAddress = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip + "/" + DEFAULT_PORT);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setVersion(DEFAULT_VERSION);
        target.setTimeout(DEFAULT_TIMEOUT);
        target.setRetries(DEFAULT_RETRY);
        return target;
    }

    private String readResponse(ResponseEvent respEvnt) {
        if (respEvnt != null && respEvnt.getResponse() != null) {
            Vector recVBs = respEvnt.getResponse().getVariableBindings();
            if (recVBs.size() > 0) {
                VariableBinding recVB = (VariableBinding) recVBs.elementAt(0);
                return recVB.getVariable().toString();
            }
        }
        return null;
    }

    private static boolean checkWalkFinished(OID targetOID, PDU pdu, VariableBinding vb) {
        boolean finished = false;
        if (pdu.getErrorStatus() != 0) {
            finished = true;
        } else if (vb.getOid() == null) {
            finished = true;
        } else if (vb.getOid().size() < targetOID.size()) {
            finished = true;
        } else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid()) != 0) {
            finished = true;
        } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
            finished = true;
        } else if (vb.getOid().compareTo(targetOID) <= 0) {
            finished = true;
        }
        return finished;
    }

    public static void main(String[] args) {
        try {
            SnmpUtil snmpUtil = new SnmpUtil("192.168.0.100", "public");
//            List<String> smmpwalk = snmpUtil.snmpWalk(".1.3.6.1.2.1.25.2.1.4");
//            smmpwalk.forEach(a->{
//                System.out.println(a);
//            });
            String[] oids = {".1.3.6.1.2.1.25.2.3.1.2", ".1.3.6.1.2.1.25.2.3.1.4",
                    ".1.3.6.1.2.1.25.2.3.1.5", ".1.3.6.1.2.1.25.2.3.1.6"};
            snmpUtil.snmpTableGet(oids);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}