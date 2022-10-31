package br.com.eduardomelle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.saga.CamelSagaService;
import org.apache.camel.saga.InMemorySagaService;

@ApplicationScoped
public class SagaRoute extends RouteBuilder {

  @Inject
  PedidoService pedidoService;

  @Inject
  CreditoService creditoService;

  @Override
  public void configure() throws Exception {
    CamelSagaService sagaService = new InMemorySagaService();
    getContext().addService(sagaService);

    from("direct:saga").saga().propagation(null);
  }

}
