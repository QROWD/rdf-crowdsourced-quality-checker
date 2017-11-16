
pybossa.taskLoaded(function(task, deferred) {
  console.log(task)
  if (!$.isEmptyObject(task)) {
    // $("#source").text(task.info.url);
    var source = task.info[0]
    var target = task.info[1]
    $("#source").attr('src', source.thumbnail);
    $("#target").attr('src', source.thumbnail);
    $("#loadingTask").hide();
    deferred.resolve(task);
  } // End if task empty
  else {
    deferred.resolve(task);
  }
});

pybossa.presentTask(function(task, deferred) {
  if (!$.isEmptyObject(task)) {
    if (task.state == 'completed') {
      $('#controls').hide();
      $('#answer').hide();
      $('#disqus_thread').hide();
      $('#taskcompleted').show();
    }

    $("#task-id").text(task.id);
    $("#loadingTask").hide();

    $('.btn-answer').off('click').on('click', function(evt) {
      console.log(evt)
      var answer = $(evt.target).attr("value");
      if (typeof answer != 'undefined') {
        console.log(answer);
        pybossa.saveTask(task.id, answer).done(function() {
          deferred.resolve();
        });
      } else {
        $("#error").show();
      }
    });

  } else {
    $(".skeleton").hide();
    $("#finish").fadeIn();
  }
});

pybossa.run('bike-rack-linker');
