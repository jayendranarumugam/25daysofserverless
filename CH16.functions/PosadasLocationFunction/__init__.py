import logging
import azure.functions as func
import json


def main(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Python HTTP trigger function processed a request.')

   
    with open('./location.json') as f:
        data = json.load(f)
        jsondata=json.dumps(data)

    return func.HttpResponse(jsondata,mimetype="application/json")   
