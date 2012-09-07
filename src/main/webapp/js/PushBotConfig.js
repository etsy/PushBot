(function($) {
    PushBotConfig =(function() {

        function _setupEventListeners() {
            $('#inputMember').change(_onMemberChange);
            $('#memberConfig input').change(_onSubmit);
            $.address.change(_onAddressChange);
        }

        function _onMemberChange(event) {
            $.address.path($('#inputMember').val());
            $.address.update();
        }

        function _onAddressChange(event) {
            var member = $.address.path().substr(1);
            if('' == member) {
                return;
            }
            $('#inputMember').unbind('change').val(member);
            $('#spanMemberName').text(member);
            _getConfigForMember(member);
        }

        function _getConfigForMember(memberName) {
            $.ajax({
                url: '/api/member/'+memberName,
                dataType: 'json',
                success: _onMemberConfig
            });
        }

        function _onMemberConfig(data) {
            $('#memberName').hide();
            $('#memberConfig').show();

            var response = data.response;
            console.log(response);

            if(response.quietDrive) {
                $('#inputQuietDrive').attr('checked', 'checked');
            }
            if(response.sendNotifoWhenUp) {
                $('#inputSendNotifoWhenUp').attr('checked', 'checked');
            }
            $('#inputNotifoUsername').val(response.notifoUsername);
            $('#inputNotifoApiSecret').val(response.notifoApiSecret);
        }

        /**
         * Executed when the form is submitted
         */
        function _onSubmit(event) {

            var member = $('#inputMember').val();
            var config = {};
            $('#memberConfig input').each(function(i,input) {
                var value = ($(input).attr('type') == 'checkbox') ?
                  $(input).is(':checked') : $(input).val();
                config[$(input).attr('name')] = value;
            });

            $.ajax({
                url: '/api/member/'+member,
                type: 'POST',
                data: JSON.stringify(config),
                dataType: 'json',
                contentType: "application/json; charset=UTF-8",
                success: function( data ) {
                    console.log(data);
                }
            });
        }

        return {
            init: function() {
                _setupEventListeners();
                $('#inputMember').select();
            }
        };
        
    })();
})(jQuery);
