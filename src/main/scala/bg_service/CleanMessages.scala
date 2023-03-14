package bg_service
import org.quartz._
import org.quartz.impl.StdSchedulerFactory

object CleanMessages extends App {
    val schedulerFactory: SchedulerFactory = new StdSchedulerFactory()
    val scheduler: Scheduler = schedulerFactory.getScheduler()

    val jobDetail: JobDetail = JobBuilder.newJob(classOf[HelloJob])
        .withIdentity("myJob", "group1")
        .build()

    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("myTrigger", "group1")
        .startNow()
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(2)
            .repeatForever())
        .build()

    scheduler.scheduleJob(jobDetail, trigger)
    scheduler.start()

    class HelloJob extends Job {
        def execute(context: JobExecutionContext): Unit = {
            println("Hello, world!")
        }
    }
}
