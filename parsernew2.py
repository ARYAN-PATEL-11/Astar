import os
import re
from datetime import datetime
from elasticsearch import Elasticsearch

# Elasticsearch credentials and endpoint
elastic_cloud_endpoint = "https://7686f03aca994d20ac7a45a2fc2071e8.us-central1.gcp.cloud.es.io:443"
username = "elastic"  # Replace with your actual username
password = "W4Ge0HNnCQEtOzsZu6FGgGyd"  # Replace with your actual password

# Create Elasticsearch client
es = Elasticsearch(
    [elastic_cloud_endpoint],
    basic_auth=(username, password),
)

# Index name
index_name = "your-index-name"

# Path to the log file
log_file_path = os.path.join(os.path.dirname(__file__), 'logs', 'application3.log')

def parse_log_line(line):
    log_pattern = re.compile(
        r'^(?P<timestamp>\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}\+\d{2}:\d{2}) '
        r'\[(?P<thread>[^\]]+)\] (?P<level>\w+)  '
        r'(?P<logger>[^\s]+) - (?P<message>.*)$'
    )

    match = log_pattern.match(line)
    if match:
        log_data = match.groupdict()
        log_data['timestamp'] = datetime.strptime(log_data['timestamp'], '%Y-%m-%dT%H:%M:%S.%f%z')
        return log_data
    else:
        return {"message": line.strip()}

# Read log file and index each line as a document in Elasticsearch
with open(log_file_path, 'r') as log_file:
    for doc_id, line in enumerate(log_file, start=1):
        doc_body = parse_log_line(line)
        res = es.index(index=index_name, id=doc_id, body=doc_body)
        print(res)

# Close the connection
es.close()