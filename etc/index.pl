use SOAP::Lite;
 
print "Connecting to Index Service...\n";
print SOAP::Lite
   -> service('http://localhost:8080/zilverline/services/IndexService?wsdl')
   -> reIndex ();
print "Indexing started...\n";
