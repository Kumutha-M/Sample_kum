@SuppressWarnings("unchecked")

      public <E, T> void makeAsyncRequest(HubbleRequestDataBean hubbleRequestDataBean,ServiceActionBean serviceActionBean, ServiceMasterDetails serviceMasterDetailData,

                  Class<E> responseType, String actionName, int actionId){

            logger.info("makeAsyncRequest method called for action ("+actionName+")");

            WSO2ResponseBean responseBean = new WSO2ResponseBean();

            WSO2Bean dataBean = new WSO2Bean();

            String finalReqString = null;

            dataBean.setDataRequest((String) serviceActionBean.getServiceData());

           

            Map<String,CustomThreadPoolTaskExecutor> serviceExecutorMap = (Map<String, CustomThreadPoolTaskExecutor>)super.getApplicationContext().getBean("asyncServiceResponseHandlerMap");

            logger.info("serviceExecutorMap :: "+serviceExecutorMap);

           

            CustomThreadPoolTaskExecutor threadPoolExecutor = serviceExecutorMap.get(getServiceName(hubbleRequestDataBean));

            logger.info("hubbleBaseThreadPoolExecutor :: "+threadPoolExecutor);

           

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

          requestFactory.setTaskExecutor(threadPoolExecutor);

          requestFactory.setConnectTimeout(serviceMasterDetailData.getServiceConnectionTimeout());

          requestFactory.setReadTimeout(serviceMasterDetailData.getServiceReadTimeout());

           

          HubbleActionDataBean hubbleActionDataBean = super.getHubbleActionCache().fetchActionDataBean(hubbleRequestDataBean.getClient(), actionName);

         

          MultiValueMap<String,String> requestHeaders = new HttpHeaders();

            requestHeaders.setAll(serviceActionBean.getServiceHeaders());

            finalReqString = JsonUtils.convertObjectToJsonWithoutExclusion(dataBean);

 

            HttpEntity<String> requestEntity =  new HttpEntity<String>(finalReqString,requestHeaders);

           

          AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(requestFactory);

         

          //super.getHubbleActionRepository().insertHubbleActionStatusLog(actionId, HubbleConstants.SERVICE_INVOKED, null);

          ListenableFuture<ResponseEntity<E>> listenableFuture = asyncRestTemplate.exchange(serviceActionBean.getServiceUrl(),

                                                                                                                                serviceActionBean.getHttpMethod(),

                                                                                                                                requestEntity,

                                                                                                                                responseType);

         

          WSO2ServiceListenableFutureCallback serviceListenableFutuerCallback =

                  new WSO2ServiceListenableFutureCallback(hubbleRequestDataBean,super.getApplicationContext(),actionName,

                                                                        serviceActionBean,hubbleActionDataBean.getActionBeanName(),actionId,finalReqString);

         

            listenableFuture.addCallback((ListenableFutureCallback<ResponseEntity<E>>) serviceListenableFutuerCallback);

           

      }

     

}
