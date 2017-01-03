select rsrc_id, 
       name, 
       rsrc_type_id, 
       rsrc_subtype_id, 
       url, 
       description, 
       secured, 
       ifnull(host, '', host) host, 
       ifnull(router_type, '', router_type) router_type
  from user_resource for xml auto, elements; 

output to 'c:\SoapWebServiceList.xml';