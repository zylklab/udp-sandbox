# udp-sandbox

## Client
The client send to localhost port 9956 events serialized in avro, located at src/main/java/net/zylklab/sandbox/udp/client . The schema is

```json
{"namespace": "net.zylklab.sandbox.udp.avro",
 "type": "record",
 "name": "MeasureRecord",
 "fields": [
     {"name": "name", "type": "string"},
     {"name": "value",  "type": ["int", "null"]},
     {"name": "timestamp", "type": ["long", "null"]}
 ]
}
```

The schema is locate in src/main/avro folder. Maven is configured to use avro plugin to create the pojo associated to the schema


## Server