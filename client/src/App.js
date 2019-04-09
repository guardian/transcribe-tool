import React, { Component } from 'react';
import {render} from 'react-dom'
import { TranscriptEditor } from '@bbc/react-transcript-editor';


class App extends Component {

    constructor(props) {
        // const urlParams = new URLSearchParams(window.location.search);

        super(props);
        this.state = {
            transcriptData: null,
            mediaUrl: '',
            fileName: ''
        };
        this.transcriptEditorRef = React.createRef();
        this.transcriptName = transcriptName;
        // this.transcriptName = urlParams.get('transcriptName');
        // this.transcriptName = "todayinfocus_1"
    }


    loadTranscript() {
        fetch(transcriptUrl).then(resp => resp.json()).then(transcript => {
            this.setState({
                mediaUrl: audioUrl,
              transcriptData: transcript
          })
        })
    }

    getTranscriptText() {
        const text = this.transcriptEditorRef.current.getEditorContent("draftjs");
        const draftJsTranscript = JSON.parse(text.data);

        const simpleOutput = draftJsTranscript.blocks.map(block => {
            return {
                speaker: block.data.speaker.replace("Speaker ", ""),
                text: block.text
            }
        });

        fetch(`/api/saveTranscript/${this.transcriptName}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(simpleOutput)
        }).then(response => response.json()).then(resp => {
            console.log("saved files to s3", resp);
            window.open(resp.textUrl);
        })
    }

    componentDidMount() {
      this.loadTranscript();
    }
  

  render() {
    return (
      <div className="App">
            <h1>Transcribe!</h1>
            {/*<button onClick={ () => this.loadTranscript() }>Load transcript</button>*/}
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