package com.rutuja.employee.service;
import com.rutuja.employee.bean.Address;
import com.rutuja.employee.bean.Department;
import com.rutuja.employee.bean.EmployeeResponce;
import com.rutuja.employee.model.EmployeeModel;
import com.rutuja.employee.repo.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.cloud.client.discovery.DiscoveryClient;


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


    public Mono<EmployeeResponce> getEmployeeById(Integer empId) throws Exception {
        if(employeeRepository.findById(empId).isPresent()) {
            log.debug("Id is present ");
            EmployeeModel employeeModel = employeeRepository.findById(empId).get();
            EmployeeResponce employeeResponce = new EmployeeResponce();
            employeeResponce.setEmpId(employeeModel.getEmpId());
            employeeResponce.setSallary(employeeModel.getSallary());
            employeeResponce.setLName(employeeModel.getLName());
            employeeResponce.setFName(employeeModel.getFName());
            Mono<Address> addressMono= webClientBuilder.build()
                    .get()
                    .uri("http://ADDRESS"+addressContextName  + employeeModel.getAddressId())
                    .retrieve()
                    .bodyToMono(Address.class);

          Mono<Department> departmentMono= webClientBuilder.build()
                    .get()
                    .uri("http://DEPARTMENT"+departmentContextName + employeeModel.getDepartmentId())
                    .retrieve()
                    .bodyToMono(Department.class);

                   Mono<EmployeeResponce> employeeResponceMono=addressMono
                    .zipWith(departmentMono)
                    .map(employeTuple->{
                        employeeResponce.setAddress(employeTuple.getT1());
                        employeeResponce.setDepartment(employeTuple.getT2());
                        return employeeResponce;
                    });
            return employeeResponceMono;
        }
       throw new Exception("Employee not found");
    }

    public Flux<EmployeeResponce> getAllEmployee() throws Exception {
        Iterable<EmployeeModel> employeeModels=employeeRepository.findAll();
        List<Mono<EmployeeResponce>> listOfEmplyee=new ArrayList<>();
        if(employeeModels!=null){
            employeeModels.forEach((employeeModel)->{
               EmployeeResponce employeeResponce= new EmployeeResponce();
                employeeResponce.setEmpId(employeeModel.getEmpId());
                employeeResponce.setSallary(employeeModel.getSallary());
                employeeResponce.setLName(employeeModel.getLName());
                employeeResponce.setFName(employeeModel.getFName());
               Mono<Address> addressMono= webClientBuilder.build()
                        .get()
                        .uri("http://ADDRESS"+addressContextName+employeeModel.getAddressId())
                        .retrieve()
                        .bodyToMono(Address.class);

               Mono<Department> departmentMono= webClientBuilder.build()
                        .get()
                        .uri("http://DEPARTMENT"+departmentContextName + employeeModel.getDepartmentId())
                        .retrieve()
                        .bodyToMono(Department.class);
              Mono<EmployeeResponce> employeeResponceMono= Mono.zip(addressMono,departmentMono).map(tuple->{
                   employeeResponce.setAddress(tuple.getT1());
                   employeeResponce.setDepartment(tuple.getT2());
                   return employeeResponce;
               });

                listOfEmplyee.add(employeeResponceMono);
            });
            return Flux.merge(listOfEmplyee);
        }
        throw new Exception("Employee not found");
    }

    public Flux<EmployeeResponce> getEmployee(List<Integer> empIds) throws Exception {
        Iterable<EmployeeModel> employeeModels=employeeRepository.findAllById(empIds);
        List<Mono<EmployeeResponce>> listOfEmplyee=new ArrayList<>();
        if(employeeModels!=null){
            employeeModels.forEach((employeeModel)->{
                EmployeeResponce employeeResponce= new EmployeeResponce();
                employeeResponce.setEmpId(employeeModel.getEmpId());
                employeeResponce.setSallary(employeeModel.getSallary());
                employeeResponce.setLName(employeeModel.getLName());
                employeeResponce.setFName(employeeModel.getFName());
                Mono<Address> addressMono=webClientBuilder.build()
                        .get()
                        .uri("http://ADDRESS"+addressContextName+employeeModel.getAddressId())
                        .retrieve()
                        .bodyToMono(Address.class);


                Mono<Department> departmentMono=webClientBuilder.build()
                        .get()
                        .uri("http://DEPARTMENT"+departmentContextName  + employeeModel.getDepartmentId())
                        .retrieve()
                        .bodyToMono(Department.class);
                Mono<EmployeeResponce> employeeResponceMono=Mono.zip(addressMono,departmentMono).map(employeeTuple->{
                    employeeResponce.setAddress(employeeTuple.getT1());
                    employeeResponce.setDepartment(employeeTuple.getT2());
                    return employeeResponce;
                });

                listOfEmplyee.add(employeeResponceMono);
            });
            return Flux.merge(listOfEmplyee);
        }
        throw new Exception("Employee not found");
    }




}
