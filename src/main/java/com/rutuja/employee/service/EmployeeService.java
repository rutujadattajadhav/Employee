package com.rutuja.employee.service;
import com.rutuja.employee.bean.Address;
import com.rutuja.employee.bean.Department;
import com.rutuja.employee.bean.EmployeeRequestBean;
import com.rutuja.employee.bean.EmployeeResponce;
import com.rutuja.employee.model.EmployeeModel;
import com.rutuja.employee.repo.EmployeeRepository;
import com.rutuja.employee.repo.SequenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;


import java.util.ArrayList;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EmployeeService {

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private EmployeeRepository employeeRepository;



    @Value("${spring.webflux.base-path-address}" )
    private String addressContextName;

    @Value("${spring.webflux.base-path-department}")
    private String departmentContextName;

    @Value("${xyz}")
    private String name;

    @Autowired()
    @LoadBalanced
    private WebClient.Builder webClientBuilder;
@Autowired
private R2dbcEntityTemplate r2dbcEntityTemplate;

@Autowired
private SequenceRepository sequenceRepository;


    public Mono<EmployeeResponce> getEmployeeById(Integer empId) {
        return employeeRepository.findById(empId)
                .switchIfEmpty(Mono.error(new Exception("Employee not found")))
                .flatMap(employeeModel -> {
                    EmployeeResponce employeeResponse = new EmployeeResponce();
                    employeeResponse.setEmpId(employeeModel.getEmpId());
                    employeeResponse.setSallary(employeeModel.getSallary());
                    employeeResponse.setLName(employeeModel.getLName());
                    employeeResponse.setFName(employeeModel.getFName());

                    Mono<Address> addressMono = webClientBuilder.build()
                            .get()
                            .uri("http://ADDRESS" + addressContextName + employeeModel.getAddressId())
                            .headers(headers -> headers.setBasicAuth("addressUser", "address"))
                            .retrieve()
                            .bodyToMono(Address.class)
                            .onErrorReturn(new Address());

                    Mono<Department> departmentMono = webClientBuilder.build()
                            .get()
                            .uri("http://DEPARTMENT" + departmentContextName + employeeModel.getDepartmentId())
                            .headers(headers -> headers.setBasicAuth("departMentuser", "department"))
                            .retrieve()
                            .bodyToMono(Department.class)
                            .onErrorReturn(new Department());

                    return addressMono.zipWith(departmentMono)
                            .map(employeeTuple -> {
                                employeeResponse.setAddress(employeeTuple.getT1());
                                employeeResponse.setDepartment(employeeTuple.getT2());
                                return employeeResponse;
                            });
                });

    }
    public Flux<EmployeeResponce> getAllEmployee() {
        Flux<EmployeeModel> employeeModels = employeeRepository.findAll();
        WebClient webClient = webClientBuilder.build();

        return employeeModels.flatMap(employeeModel -> {
            EmployeeResponce employeeResponse = new EmployeeResponce();
            employeeResponse.setEmpId(employeeModel.getEmpId());
            employeeResponse.setSallary(employeeModel.getSallary());
            employeeResponse.setLName(employeeModel.getLName());
            employeeResponse.setFName(employeeModel.getFName());

            Mono<Address> addressMono = webClient
                    .get()
                    .uri("http://ADDRESS" + addressContextName + employeeModel.getAddressId())
                    .headers(headers -> headers.setBasicAuth("addressUser", "address"))
                    .retrieve()
                    .bodyToMono(Address.class)
                    .onErrorResume(e -> {
                        log.error("Failed to fetch address", e);
                        return Mono.empty();
                    });

            Mono<Department> departmentMono = webClient
                    .get()
                    .uri("http://DEPARTMENT" + departmentContextName + employeeModel.getDepartmentId())
                    .headers(headers ->headers.setBasicAuth("departMentuser", "department"))
                    .retrieve()
                    .bodyToMono(Department.class)
                    .onErrorResume(e -> {
                        log.error("Failed to fetch department", e);
                        return Mono.empty();
                    });

            return Mono.zip(addressMono, departmentMono)
                    .map(tuple -> {
                        employeeResponse.setAddress(tuple.getT1());
                        employeeResponse.setDepartment(tuple.getT2());
                        return employeeResponse;
                    });
        }).switchIfEmpty(Mono.error(new Exception("No employees found")));
    }

    public Flux<EmployeeResponce> getEmployee(List<Integer> empIds) {
        return Flux.fromIterable(empIds)
                .flatMap(empId -> employeeRepository.findById(empId)
                        .switchIfEmpty(Mono.error(new Exception("Employee not found")))
                        .flatMap(employeeModel -> {
                            EmployeeResponce employeeResponse = new EmployeeResponce();
                            employeeResponse.setEmpId(employeeModel.getEmpId());
                            employeeResponse.setSallary(employeeModel.getSallary());
                            employeeResponse.setLName(employeeModel.getLName());
                            employeeResponse.setFName(employeeModel.getFName());

                            Mono<Address> addressMono = webClientBuilder.build()
                                    .get()
                                    .uri("http://ADDRESS" + addressContextName + employeeModel.getAddressId())
                                    .headers(headers -> headers.setBasicAuth("addressUser", "address"))
                                    .retrieve()
                                    .bodyToMono(Address.class);

                            Mono<Department> departmentMono = webClientBuilder.build()
                                    .get()
                                    .uri("http://DEPARTMENT" + departmentContextName + employeeModel.getDepartmentId())
                                    .headers(headers -> headers.setBasicAuth("departMentuser", "department"))
                                    .retrieve()
                                    .bodyToMono(Department.class);

                            return Mono.zip(addressMono, departmentMono)
                                    .map(employeeTuple -> {
                                        employeeResponse.setAddress(employeeTuple.getT1());
                                        employeeResponse.setDepartment(employeeTuple.getT2());
                                        return employeeResponse;
                                    });
                        }))
                .onErrorResume(throwable -> Flux.error(new RuntimeException("Unable to fetch employee details", throwable)));
    }


    public Mono<String> saveEmployee(EmployeeRequestBean employeeRequestBean) {
        EmployeeModel employeeModel = new EmployeeModel();
        employeeModel.setFName(employeeRequestBean.getFirstname());
        employeeModel.setLName(employeeRequestBean.getLastname());
        employeeModel.setSallary(employeeRequestBean.getSallary());
        employeeModel.setDepartmentId(employeeRequestBean.getDepartment().getDepartmentId());

        Mono<Address> addressMono = webClientBuilder.build()
                .post()
                .uri("http://ADDRESS" + addressContextName + "/saveAddress")
                .bodyValue(employeeRequestBean.getAddress())
                .headers(headers -> headers.setBasicAuth("addressUser", "address"))
                .retrieve()
                .bodyToMono(Address.class);

        Mono<Integer> sequenceMono = sequenceRepository.selectValue();

         return addressMono.flatMap(address -> {
            //employeeRequestBean.getAddress().setAddressId(address.getAddressId());
            employeeModel.setAddressId(address.getAddressId());
            return sequenceMono.switchIfEmpty(Mono.error(new Exception("Not Found")))
                    .onErrorResume(error -> {
                        log.error("Error during employee save: ", error);
                        return Mono.error(new Exception("Address not saved"));
                    })
                    .flatMap(sequenceValue -> {
                        employeeModel.setEmpId(sequenceValue);
                        return r2dbcEntityTemplate.insert(employeeModel)
                                .switchIfEmpty(Mono.error(new Exception("Employee not saved")))
                                .flatMap(employee -> sequenceRepository.updateSequenceemployee(employeeModel.getEmpId() + 1)
                                        .flatMap(sequence -> Mono.just("Employee saved successfully"))
                                        .switchIfEmpty(Mono.just("Employee not saved")))
                                .onErrorResume(error -> {
                                    log.error("Error during user updation: ", error);
                                    return Mono.just("Please try after some time or contact system admin");
                                });
                    });
        }).onErrorResume(error -> {
            log.error("Error during employee save: ", error);
            return Mono.just("error accour during employee save");
        });
    }

    public Mono<String> updateEmployee(EmployeeRequestBean employeeRequestBean) {
        EmployeeModel employeeModel = new EmployeeModel();
        employeeModel.setFName(employeeRequestBean.getFirstname());
        employeeModel.setLName(employeeRequestBean.getLastname());
        employeeModel.setSallary(employeeRequestBean.getSallary());
        employeeModel.setDepartmentId(employeeRequestBean.getDepartment().getDepartmentId());
        employeeModel.setEmpId(employeeRequestBean.getEmpId());
        Mono<Address> addressMono = webClientBuilder.build()
                .put()
                .uri("http://ADDRESS" + addressContextName + "/updateAddress")
                .bodyValue(employeeRequestBean.getAddress())
                .headers(headers -> headers.setBasicAuth("addressUser", "address"))
                .retrieve()
                .bodyToMono(Address.class);

        return addressMono.flatMap(address -> {
            employeeModel.setAddressId(address.getAddressId());
            return r2dbcEntityTemplate.update(employeeModel)
                    .switchIfEmpty(Mono.error(new Exception("Employee not updtated")))
                    .flatMap(employee -> Mono.just("Employee updated successfully"))
                    .onErrorResume(error -> {
                        log.error("Error during user updation: ", error);
                        return Mono.just("Please try after some time or contact system admin");
                    });
        }).onErrorResume(error -> {
            log.error("Error during employee updation: ", error);
            return Mono.just("error accour during employee update");
        });
    }

    public Mono<String> deleteEmployee(Integer empId) {
        return employeeRepository.findById(empId)
                .switchIfEmpty(Mono.error(new Exception("Employee not found")))
                .flatMap(employeeModel -> {
                    Mono<Void> addressMono = webClientBuilder.build()
                            .delete()
                            .uri("http://ADDRESS" + addressContextName + "/deleteAddress/" + employeeModel.getAddressId())
                            .headers(headers -> headers.setBasicAuth("addressUser", "address"))
                            .retrieve()
                            .bodyToMono(Void.class);

                    return r2dbcEntityTemplate.delete(employeeModel)
                            .then(addressMono)
                            .thenReturn("Employee deleted successfully");
                })
                .onErrorResume(error -> {
                    log.error("Error during employee deletion: ", error);
                    return Mono.just("Unable to delete employee");
                });
    }
}
