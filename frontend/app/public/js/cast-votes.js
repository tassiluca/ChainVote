function requestCode() {
    const urlToRequestCode = window.location.origin + '/elections/code';
    const electionId = $('#voteCodeForm input[name="election-id"]').val();

    const data = {
        electionId: electionId,
    };

    $.ajax({
        type: "POST",
        url: urlToRequestCode,
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8"
    }).done(function(response) {
        if(response.success) {
            $('#voteForm input[name="otc"]').val(response.code);
        }
    }).fail(function(error) {
        console.error('Error requesting code:', error.message);
    });
}

function sendVote() {
    const urlToSendVote = window.location.origin + '/elections/vote/';

    const electionCode = $('#voteForm input[type="text"]').val();
    const choice = $('#voteForm input[name="choiceRadio"]:checked').val();

    if (!electionCode || !choice) {
        alert('Please fill in all fields');
        return;
    }

    const data = {
        code: electionCode,
        choice: choice
    };

    $.ajax({
        type: "POST",
        url: urlToSendVote + '<%= responseData.data.electionId %>',
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8"
    }).done(function(response) {
        alert(response.message);
        if(response.success){
            $('#voteForm input[type="text"]').val('');
            $('#voteForm input[name="choiceRadio"]:checked').prop('checked', false);
            window.location.href = response.url;
        }
    }).fail(function(e) {
        alert('Error submitting vote. Please try again.');
    });
}

$(document).ready(function() {
    $('#voteCodeForm').submit(function(event) {
        event.preventDefault();
        requestCode();
    });

    $('#voteForm').submit(function(event) {
        event.preventDefault();
        sendVote();
    });
});
