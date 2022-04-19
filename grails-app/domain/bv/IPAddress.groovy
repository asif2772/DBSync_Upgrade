package bv

class IPAddress {

    String ip
    String urlPattern
    int sequenceOrder
    static constraints = {
    }

    static mapping = {
        sort 'sequenceOrder'
    }
}
