import logging
import azure.functions as func
import json


def main(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Python HTTP trigger function processed a request.')

   
    with open('./location.json') as f:
        jsondata = json.load(f)

    return func.HttpResponse(jsondata,mimetype="application/json")   
