package com.zuehlke.zSport.Batch;

import com.zuehlke.zSport.Model.User;
import com.zuehlke.zSport.Repository.EventRepository;
import com.zuehlke.zSport.Repository.UserRepository;
import com.zuehlke.zSport.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;


import javax.mail.MessagingException;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class NotificationBatchConfiguration {

    private final static Logger LOG = LoggerFactory.getLogger(NotificationBatchConfiguration.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EmailService emailService;


    @Bean
    public NotificationEmailProcessor processor() {
        return new NotificationEmailProcessor(userRepository, eventRepository);
    }

    @Bean
    ItemReader<User> reader(DataSource dataSource) {
        JdbcCursorItemReader<User> databaseReader = new JdbcCursorItemReader<>();
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql("SELECT id FROM users");
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(User.class));
        return databaseReader;
    }

    private ItemWriter<NotificationEmail> writer() {
        ItemWriter<NotificationEmail> writer = new ItemWriter<NotificationEmail>() {
            @Override
            public void write(List<? extends NotificationEmail> emails) {
                if (emails != null) {
                    for (NotificationEmail email : emails) {
                        try {
                            emailService.send(email);
                            User user = userRepository.findByEmail(email.getRecipient());
                            user.setLastNotificationTime(LocalDateTime.now());
                            userRepository.saveAndFlush(user);
                        } catch (MessagingException e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }

            }
        };
        return writer;
    }


    @Bean
    public Job notifyUserJob(Step notifyUser) {
        return jobBuilderFactory.get("notifyUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(notifyUser)
                .end()
                .build();
    }


    @Bean
    public Step notifyUser(DataSource dataSource) {
        return stepBuilderFactory.get("notifyUser")
                .<User, NotificationEmail>chunk(10)
                .reader(reader(dataSource))
                .processor(processor())
                .writer(writer())
                .build();
    }

}
