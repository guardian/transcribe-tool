import React, { Component } from 'react';
import {render} from 'react-dom'
import { TranscriptEditor } from '@bbc/react-transcript-editor';


class App extends Component {

    constructor(props) {


      console.log("heheheheh");
        const urlParams = new URLSearchParams(window.location.search);

        super(props);
        this.state = {
            transcriptData: null,
            mediaUrl: '',
            fileName: ''
        }
        this.transcriptEditorRef = React.createRef();
        this.transcriptName = urlParams.get('transcriptName');
    }


    loadTranscript() {
        fetch(transcriptUrl).then(resp => resp.json()).then(transcript => {
            this.setState({
              transcriptData: transcript,
              mediaUrl: audioUrl,
          })
        })
    }

    getTranscriptText() {
        const text = this.transcriptEditorRef.current.getEditorContent("txt");
        console.log(text.data);
        fetch(`/api/saveTranscript/${this.transcriptName}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(text)
        }).then(response => console.log(response))
    }

    componentDidMount() {
      this.loadTranscript();
    }
  

  render() {
    return (
      <div className="App">
            <h1>Transcribe!</h1>
            <button onClick={ () => this.loadTranscript() }>Load transcript</button>
            <button onClick={ () => this.getTranscriptText() }>Get transcript text</button>
            <TranscriptEditor
                transcriptData={this.state.transcriptData} // Transcript json
                mediaUrl={this.state.mediaUrl}// string url to media file - audio or video
                isEditable={true}// set to true if you want to be able to edit the text
                sttJsonType={ 'amazontranscribe' } // the type of STT Json transcript supported.
                // handleAnalyticsEvents={ this.handleAnalyticsEvents } // optional - if you want to collect analytics events.
                fileName={this.state.fileName} // optional - used for saving and retrieving local storage blob files
                title='Transcribe'
                ref={ this.transcriptEditorRef } // optional - if you want to have access to internal functions such as retrieving content from the editor. eg to save to a server/db.
            />
      </div>
    );
  }
}

render(<App />, document.getElementById('root'));